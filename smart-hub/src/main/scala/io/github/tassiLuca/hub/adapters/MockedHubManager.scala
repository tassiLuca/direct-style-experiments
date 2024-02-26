package io.github.tassiLuca.hub.adapters

import gears.async.TaskSchedule.RepeatUntilFailure
import gears.async.{Async, AsyncOperations, ReadableChannel, Task}
import io.github.tassiLuca.hub.adapters.ui.DashboardUI
import io.github.tassiLuca.hub.core.{LuminosityEntry, TemperatureEntry}
import io.github.tassiLuca.rears.groupBy

import scala.language.postfixOps

/** A concrete hub manager, mocking sources with graphical views. */
class MockedHubManager(using Async, AsyncOperations):

  private val ui = DashboardUI()
  private val sensorsSource = GraphicalSource()
  private val thermostatHub = new MockedThermostatManager() with SwingDashboardService(ui)
  private val lightingHub = new MockedLightingManager() with SwingDashboardService(ui)

  def run(): Unit =
    val channelBySensor = sensorsSource.publishingChannel.groupBy(e => e.getClass)
    Task {
      channelBySensor.read() match
        case Right((clazz, c)) if clazz == classOf[TemperatureEntry] =>
          thermostatHub.run(c.asInstanceOf[ReadableChannel[TemperatureEntry]])
        case Right((clazz, c)) if clazz == classOf[LuminosityEntry] =>
          lightingHub.run(c.asInstanceOf[ReadableChannel[LuminosityEntry]])
        case _ => ()
    }.schedule(RepeatUntilFailure()).run
    sensorsSource.asRunnable.run.await
