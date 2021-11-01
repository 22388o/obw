package immortan.utils

import java.text._

import fr.acinq.eclair._


object Denomination {
  val locale = new java.util.Locale("en", "US")
  val symbols = new DecimalFormatSymbols(locale)
  val formatFiatPrecise = new DecimalFormat("#,###,###.##")
  val formatFiat = new DecimalFormat("#,###,###")
  val btcFactor = 100000000000L
  val satFactor = 1000L

  formatFiatPrecise setDecimalFormatSymbols symbols
  formatFiat setDecimalFormatSymbols symbols

  def mast2BtcBigDecimal(msat: MilliSatoshi): BigDecimal = BigDecimal(msat.toLong) / btcFactor

  def btcBigDecimal2MSat(btc: BigDecimal): MilliSatoshi = (btc * btcFactor).toLong.msat
}

trait Denomination {
  def parsed(msat: MilliSatoshi, mainColor: String, zeroColor: String): String

  def fromMsat(amount: MilliSatoshi): BigDecimal = BigDecimal(amount.toLong) / Denomination.satFactor

  def parsedWithSign(msat: MilliSatoshi, mainColor: String, zeroColor: String): String = parsed(msat, mainColor, zeroColor) + "\u00A0" + sign

  def directedWithSign(incoming: MilliSatoshi, outgoing: MilliSatoshi, inColor: String, outColor: String, zeroColor: String, isIncoming: Boolean): String = {
    if (isIncoming && incoming == 0L.msat) parsedWithSign(incoming, inColor, zeroColor)
    else if (isIncoming) "+&#160;" + parsedWithSign(incoming, inColor, zeroColor)
    else if (outgoing == 0L.msat) parsedWithSign(outgoing, outColor, zeroColor)
    else "-&#160;" + parsedWithSign(outgoing, outColor, zeroColor)
  }

  val fmt: DecimalFormat
  val sign: String
}

object SatDenomination extends Denomination { me =>
  val fmt: DecimalFormat = new DecimalFormat("###,###,###.###")
  fmt.setDecimalFormatSymbols(Denomination.symbols)
  val sign = "sat"

  def parsed(msat: MilliSatoshi, mainColor: String, zeroColor: String): String = {
    val basicMsatSum = fmt.format(BigDecimal(msat.toLong) / Denomination.satFactor)
    val (whole, decimal) = basicMsatSum splitAt basicMsatSum.indexOf(".")

    if (decimal == basicMsatSum) s"<font color=$mainColor>$basicMsatSum</font>"
    else s"<font color=$mainColor>$whole<small>$decimal</small></font>"
  }
}