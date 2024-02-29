package io.github.tassiLuca.analyzer.client

import io.github.tassiLuca.analyzer.commons.client.{AnalyzerView, AppController, OrganizationReport}
import io.github.tassiLuca.analyzer.commons.lib.RepositoryReport
import io.github.tassiLuca.analyzer.lib.Analyzer
import monix.execution.CancelableFuture

object AppController:
  def monadic: AppController = MonadicAppController()

  private class MonadicAppController extends AppController:

    import monix.execution.Scheduler.Implicits.global
    private val view = AnalyzerView.gui(this)
    private val analyzer = Analyzer.ofGitHub()
    private var currentComputation: Option[CancelableFuture[Unit]] = None

    view.run()

    override def runSession(organizationName: String): Unit =
      var organizationReport: OrganizationReport = (Map(), Set())
      val f = analyzer.analyze(organizationName) { report =>
        organizationReport = organizationReport.mergeWith(report)
        view.update(organizationReport)
      }.value.runToFuture.map { case Left(value) => view.error(value); case Right(_) => view.endComputation() }
      currentComputation = Some(f)

    override def stopSession(): Unit = currentComputation foreach (_.cancel())
