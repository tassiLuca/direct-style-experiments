package io.github.tassiLuca.analyzer.client

import gears.async.default.given
import gears.async.Async

@main def directAnalyzerLauncher(): Unit =
  Async.blocking:
    AppController()
    Thread.sleep(Long.MaxValue)
