package ru.mairwunnx.mojoins

import org.bstats.bukkit.Metrics
import org.bstats.charts.AdvancedPie
import org.bstats.charts.SingleLineChart
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Callable
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * What bStats reads every 30 minutes, from its own thread. The server thread counts; bStats drains
 * the counts, so each send covers the period since the last one. Only language codes and fixed
 * words leave the server: no names, addresses, or ids.
 */
class MetricsCounts {
  private val languages = ConcurrentHashMap<String, AtomicLong>()
  private val sources = ConcurrentHashMap<String, AtomicLong>()
  private val firstJoins = AtomicLong()

  /** One language pick: the code it landed on and how, `ip`, `client`, `choice`, or `default`. */
  fun pick(language: String, source: String) {
    languages.computeIfAbsent(safeCode(language)) { AtomicLong() }.incrementAndGet()
    sources.computeIfAbsent(source) { AtomicLong() }.incrementAndGet()
  }

  fun firstJoin() {
    firstJoins.incrementAndGet()
  }

  fun drainLanguages(): Map<String, Int> = drain(languages)
  fun drainSources(): Map<String, Int> = drain(sources)
  fun drainFirstJoins(): Int = firstJoins.getAndSet(0).clampToInt()

  private fun drain(counts: ConcurrentHashMap<String, AtomicLong>): Map<String, Int> = buildMap {
    counts.forEach { (key, count) -> count.getAndSet(0).takeIf { it > 0 }?.let { put(key, it.clampToInt()) } }
  }

  private fun Long.clampToInt() = coerceAtMost(Int.MAX_VALUE.toLong()).toInt()

  companion object {
    private val CODE = Regex("[a-z]{2,3}(_[a-z0-9]{2,4})?")

    /** A server can name a language file anything; only what looks like a language code goes out. */
    fun safeCode(code: String): String = code.takeIf { CODE.matches(it) } ?: "other"
  }
}

/** bStats for Mo’Joins. Any failure in it turns into one warning and never reaches the plugin. */
class PluginMetrics(private val plugin: MoJoinsPlugin) {
  val counts = MetricsCounts()
  private var metrics: Metrics? = null
  private val warned = AtomicBoolean()

  /** Starts or stops bStats to match `metrics` in config.yml. */
  fun apply(enabled: Boolean) {
    if (!enabled || BSTATS_ID == 0) return stop()
    if (metrics != null) return
    metrics = safely(null) {
      Metrics(plugin, BSTATS_ID).apply {
        addCustomChart(AdvancedPie("player_languages", guarded(emptyMap(), counts::drainLanguages)))
        addCustomChart(AdvancedPie("language_source", guarded(emptyMap(), counts::drainSources)))
        addCustomChart(SingleLineChart("first_joins", guarded(0, counts::drainFirstJoins)))
      }
    }
  }

  fun stop() {
    metrics?.let { safely(Unit) { it.shutdown() } }
    metrics = null
  }

  private fun <T> guarded(fallback: T, read: () -> T): Callable<T> = Callable { safely(fallback, read) }

  private inline fun <T> safely(fallback: T, block: () -> T): T = try {
    block()
  } catch (e: Throwable) {
    if (warned.compareAndSet(false, true)) plugin.log.warn("bStats failed, Mo’Joins goes on without it: $e")
    fallback
  }

  companion object {
    /** The plugin's id on bstats.org; 0 keeps bStats off. */
    const val BSTATS_ID = 0
  }
}
