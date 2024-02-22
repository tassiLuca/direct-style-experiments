package io.github.tassiLuca.smarthome.core

trait DashboardComponent:

  val dashboard: Dashboard

  trait Dashboard:
    def temperatureUpdated(newTemperature: Temperature): Unit
    def onHeaterNotified(): Unit
    def offHeaterNotified(): Unit
    def alertNotified(msg: String): Unit
