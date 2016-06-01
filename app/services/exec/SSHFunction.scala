package services.exec

/**
  * Created by camilosampedro on 12/05/16.
  */
object SSHFunction {
  val translateOS = Map(
    "Arch" -> "Arch",
    "ManjaroLinux" -> "Arch",
    "Debian"->"Debian",
    "Ubuntu" -> "Debian",
    "LinuxMint" -> "Debian"
  )

  val dummy = """echo "Ping from Aton""""

  val macOrders = Map(
    "Arch" -> Seq(
      """ifconfig enp3s0 2>/dev/null|awk '/ether/ {print $2}'"""
    ),
    "Debian" -> Seq(
      "ifconfig eth0 2>/dev/null|awk '/direcciónHW/ {print $5}'",
      "ifconfig enp3s0 2>/dev/null|awk '/ether/ {print $2}'",
      "ifconfig eth0 2>/dev/null|awk '/HWaddr/ {print $5}'",
      "ifconfig enp3s0 2>/dev/null|awk '/direcciónHW/ {print $5}'",
      "ifconfig enp3s0 2>/dev/null|awk '/HWaddr/ {print $5}'"
    )).withDefaultValue(Seq(
    "ifconfig eth0 2>/dev/null|awk '/direcciónHW/ {print $5}'",
    "ifconfig enp3s0 2>/dev/null|awk '/ether/ {print $2}'",
    "ifconfig eth0 2>/dev/null|awk '/HWaddr/ {print $5}'",
    "ifconfig enp3s0 2>/dev/null|awk '/direcciónHW/ {print $5}'",
    "ifconfig enp3s0 2>/dev/null|awk '/HWaddr/ {print $5}'"
  ))

  val upgradeOrder = Map(
    "Arch"->"""sudo pacman -Syu --noconfirm""",
    "Debian"->"""apt-get update; apt-get upgrade --assume-yes"""
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
  def blockPageOrder(page: String) = s"""cp /etc/hosts{,.bak} && echo "127.0.0.1 $page" >> /etc/hosts && echo "Added page" """

  def COMPUTER_WAKEUP_ORDER(sufijoIPSala: Int, mac: String) = "wakeonlan -i 192.168." + sufijoIPSala + ".255 " + mac


  def notificationOrder(user: String, message: String) = generateOrderForUser(user, s"""zenity --info --title="Mensaje desde Aton" --text="$message"""")

  def generateOrderForUser(usuario: String, orden: String) = "sudo -u " + usuario + " DISPLAY=:0.0 " + orden

  def sudofy(comando: String) = "sudo -S -p '' -- sh -c '" + comando.replace("$", "\\$").replace("'", "\"") + "'"
}
