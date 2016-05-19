package services.exec

/**
  * Created by camilosampedro on 12/05/16.
  */
object SSHFunction {
  val dummy = """echo "Ping from Aton""""

  val macOrders = Seq(
    "ifconfig eth0 2>/dev/null|awk '/direcciónHW/ {print $5}'",
    "ifconfig enp3s0 2>/dev/null|awk '/ether/ {print $2}'",
    "ifconfig eth0 2>/dev/null|awk '/HWaddr/ {print $5}'",
    "ifconfig enp3s0 2>/dev/null|awk '/direcciónHW/ {print $5}'",
    "ifconfig enp3s0 2>/dev/null|awk '/HWaddr/ {print $5}'"
  )

  val operatingSystemCheck = """lsb_release -a 2>/dev/null | grep "Distributor ID" | awk '{print $3}'"""

  val IP_ORDER = "ifconfig eth0 2>/dev/null|awk '/Direc. inet:/ {print $2}'|sed 's/inet://'"
  val ALT_IP_ORDER = "ifconfig eth0 |awk '/inet addr:/ {print $2}'|sed 's/addr://'"
  val HOST_ORDER = "cat /etc/hostname"
  val ROOT_VERIFICATION_ORDER = "id -u"
  val USER_IDENTIFIER_ORDER = "whoamai"
  val shutdownOrder = "shutdown -h now"
  val REBOOT_ORDER = "shutdown -r now"
  val IP_OBTAINING_ORDER = "ifconfig eth0 2>/dev/null|awk '/Direc. inet:/ {print $2}'|sed 's/inet://'"
  val userListOrder = "who | cut -d' ' -f1 | sort | uniq"
  val upgradeOrder = """sudo pacman -Syu --noconfirm"""//"apt-get update; apt-get upgrade --assume-yes"

  def COMPUTER_WAKEUP_ORDER(sufijoIPSala: Int, mac: String) = "wakeonlan -i 192.168." + sufijoIPSala + ".255 " + mac


  def NOTIFICACION_ORDER(usuario: String, mensaje: String) = GENERATE_ORDER_FOR_USER(usuario, "zenity --info --title=\"Mensaje desde Aton\" --text=\"" + mensaje + "\"")

  def GENERATE_ORDER_FOR_USER(usuario: String, orden: String) = "sudo -u " + usuario + " DISPLAY=:0.0 " + orden

  def SUDO(comando: String) = "sudo -S -p '' -- sh -c '" + comando.replace("$", "\\$").replace("'", "\"") + "'"
}
