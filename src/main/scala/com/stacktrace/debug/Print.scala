/*Licensed under CC0. Under the public domain where applicable.*/
package com.stacktrace.debug

/**
  * Poor man's debugging utility. Works best when run in "Debug" mode. For best results, use an IDE: http://i.imgur.com/gfi1OYx.png
  */
object Print {

  /**
    * Stack offset is 2 because the first row in the stack trace is Thread and the second row is internal call.
    */
  private val stackOffset = 2

  @volatile private var debugMode = true

  /**
    * Turns on debug mode. Print statements with stack traces are visible and fatal assertions are enabled.
    */
  def debugModeOn = {
    debugMode = true
  }

  /**
    * Turns off debug mode. Print statements with stack traces are invisible and fatal assertions are disabled.
    */
  def debugModeOff = {
    debugMode = false
  }

  /**
    * Prints a line with a stack trace. For sample usage, see: http://i.imgur.com/gfi1OYx.png
    *
    * @param message the message to print
    */
  def line(message: String) = {
    if(debugMode) {
      val stack = Thread.currentThread().getStackTrace
      val stackLine = stack(stackOffset)
      val printLine1 = "\"" + message + "\"" + " in thread " + Thread.currentThread().getName + ":" + "\n"
      val printLine2 = "  at " + stackLine
      System.err.println(
        printLine1 + printLine2
      )
      System.err.println("")
    }
  }

  /**
    * Prints a line with a certain numbers of rows of stack trace
    *
    * @param message the message to print
    * @param numStackLines the numbers of rows of stack trace
    */
  def line(message: String, numStackLines: Int) = {
    require(numStackLines >= 0, "the number of lines must be positive.")
    if(debugMode) {
      val stack = Thread.currentThread().getStackTrace
      var toPrint = "\"" + message + "\"" + " in thread " + Thread.currentThread().getName + ":"
      for (row <- 0 to Math.min(numStackLines - 1, stack.length - 1 - stackOffset)) {
        val lineNumber = stackOffset + row
        val stackLine = stack(lineNumber)
        toPrint += "\n" + "  at " + stackLine
      }
      System.err.println(toPrint)
      System.err.println("")
    }
  }

  /**
    * Fatal assertion with stack trace. Exits with error code "7"
    *
    * @param assertion test to see if this is true
    * @param message message to print if this if false
    */
  def asrt(assertion: Boolean, message: String) = {
    if(debugMode) {
      if(! assertion) {
        var toPrint = "\"" + message + "\"" + " in thread " + Thread.currentThread().getName + ":"
        val stack = Thread.currentThread().getStackTrace
        for (row <- 0 to stack.length - 1 - stackOffset) {
          val lineNumber = stackOffset + row
          val stackLine = stack(lineNumber)
          toPrint += "\n" + "  at " + stackLine
        }
        toPrint += "\n" + "  This application failed due to a fatal assertion."
        System.err.println(toPrint)
        System.err.println("")
        System.exit(7)
      }
    }
  }

  /* This main method is just for testing.*/
  def main(args: Array[String]) = {

    line("Line with three lines of stack trace", 3)
    line("Line with one line of stack trace")
    asrt(false, "failure")
  }

}
