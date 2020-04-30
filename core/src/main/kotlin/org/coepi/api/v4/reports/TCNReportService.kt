package org.coepi.api.v4.reports

import java.time.Clock
import java.time.LocalDate
import org.coepi.api.common.time.toUtcLocalDate
import org.coepi.api.v4.dao.TCNReportRecord
import org.coepi.api.v4.dao.TCNReportsDao
import org.coepi.api.v4.toInterval

interface TCNReportService {
    /**
     * Return a list of TCN reports. If [maybeDate] is null, the current UTC date will be used. If
     * [maybeIntervalNumber] is null, the current interval will be used.
     */
    fun getReports(maybeDate: LocalDate?, maybeIntervalNumber: Long?): List<TCNReportRecord>

    fun getReports(date: LocalDate, intervalNumber: Long): List<TCNReportRecord>

    fun saveReport(reportData: ByteArray): TCNReportRecord
}

class TCNReportServiceImpl(
    private val clock: Clock,
    private val reportsDao: TCNReportsDao
) : TCNReportService {
    override fun getReports(
        date: LocalDate,
        intervalNumber: Long
    ): List<TCNReportRecord> =
        reportsDao.queryReports(date, intervalNumber)

    override fun getReports(
        maybeDate: LocalDate?,
        maybeIntervalNumber: Long?
    ): List<TCNReportRecord> {
        val now = clock.instant()

        return getReports(
            date = maybeDate ?: now.toUtcLocalDate(),
            intervalNumber = maybeIntervalNumber ?: now.toInterval()
        )
    }

    override fun saveReport(reportData: ByteArray): TCNReportRecord {
        val now = clock.instant()

        // TODO: Validate reportData and signature
        return reportsDao.addReport(
            reportData = reportData,
            date = now.toUtcLocalDate(),
            intervalNumber = now.toInterval(),
            timestamp = now.toEpochMilli()
        )
    }
}
