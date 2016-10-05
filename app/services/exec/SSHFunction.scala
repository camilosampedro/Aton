package services.exec

/**
  * SSH functions and orders. They are Strings that could be executed on a remote computer via SSH.
  * @author Camilo Sampedro <camilo.sampedro@udea.edu.co>
  */
object SSHFunction {

  /**
    * Translates OS Strings to its base distribution
    */
  val translateOS = Map(
    "Arch" -> "Arch",
    "ManjaroLinux" -> "Arch",
    "Debian" -> "Debian",
    "Ubuntu" -> "Debian",
    "LinuxMint" -> "Debian"
  )

  /**
    * A simple instruction to test SSH conections.
    * Shows: "Ping from Aton"
    * Return: 0 on success, other on failure
    */
  val dummy =
  """echo "Ping from Aton""""

  /**
    * Look for MAC address based on ifconfig
    * Shows: Mac address if found, else empty String
    * Returns: 0 on success, other on failure
    */
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

  /**
    * Upgrades system packages with the main system's package manager.
    * Shows: Upgrade output (Depending on package manager)
    * Returns: 0 on success, other on failure
    */
  val upgradeOrder = Map(
    "Arch" ->"""sudo pacman -Syu --noconfirm""",
    "Debian" ->"""apt-get update; apt-get upgrade --assume-yes"""
  )

  /**
    * Look for what distribution is running on the computer.
    * Shows: Distribution String
    * Return: 0 on success, other on failure
    */
  val operatingSystemCheck =
  """lsb_release -a 2>/dev/null | grep "Distributor ID" | awk '{print $3}'"""

  // IP checking orders
  val ipExtractingOrder = "ifconfig eth0 2>/dev/null|awk '/Direc. inet:/ {print $2}'|sed 's/inet://'"
  val alternativeIpExtractingOrder = "ifconfig eth0 |awk '/inet addr:/ {print $2}'|sed 's/addr://'"
  val IP_OBTAINING_ORDER = "ifconfig eth0 2>/dev/null|awk '/Direc. inet:/ {print $2}'|sed 's/inet://'"

  /**
    * Look for the computer hostname
    * Shows: Content of /etc/hostname
    * Return: 0 on success, other on failures
    */
  val hostExtractingOrder = "cat /etc/hostname"

  /**
    * Check user id. If the user is root, it shows "0"
    * Shows: User id (Integer)
    * Return: 0 on success, other on failures
    */
  val userIdentifierExtractingOrder = "id -u"

  /**
    * Look for current user's username
    * Shows: User's username String
    * Return: 0 on success, other on failures
    */
  val userNameExtractingOrder = "whoamai"

  /**
    * Shuts down the computer immediately. It closes connections and they need sometimes to be closed manually.
    * Shows: Nothing.
    * Return: Nothing.
    */
  val shutdownOrder = "shutdown -h now"
  /**
    * Reboots the computer immediately. It closes connections and they need sometimes to be closed manually.
    * Shows: Nothing.
    * Return: Nothing.
    */
  val rebootOrder = "shutdown -r now"

  /**
    * List all unique users logged in the computer.
    * Shows: A multiline String of users logged in the computer, one user per line
    * Return: 0 on success, other on failures
    */
  val userListOrder = "who | cut -d' ' -f1 | sort | uniq"

  /**
    * Blocks a page on the computer. It redirects the page to localhost (127.0.0.1) on /etc/hosts file.
    * Shows: An empty String
    * Return: 0 on success, other on failures
    *
    * @param page Page url (www.example.com) to be blocked
    * @return Order String
    */
  def blockPageOrder(page: String) =
  s"""cp /etc/hosts /etc/hosts.bak && echo "127.0.0.1 $page" >> /etc/hosts && echo "Added page" """

  /**
    * Wakes up computer (Turns it on). This is a test, it has not been achieved without being on the same network, so it
    * is not included on the main web interface.
    * Shows: @TODO
    * Return: 0 on success
    *
    * @param roomSuffix 192, 193 or 194 (LIS, tested room networks)
    * @param mac Computer to be awaken's MAC
    * @return Order String
    */
  def computerWakeUpOrder(roomSuffix: Int, mac: String) = "wakeonlan -i 192.168." + roomSuffix + ".255 " + mac

  /**
    * Creates a window with the given message to the given logged user. It currently only support --info, but you can
    * see all the available options with "man zenity". It is not closed until the user click "Okay".
    * Shows: Nothing
    * Return 0 on success, other on failures
    *
    * @param user Target user's username
    * @param message Message String to be displayed on the zenity window
    * @return Order String
    */
  def notificationOrder(user: String, message: String) = generateOrderForUser(user, s"""zenity --info --title="Message from Aton" --text="$message"""")

  /**
    * Encapsulates the order, forcing it to be executed by the given user.
    * @param user Target user's username
    * @param order Order to be executed by user
    * @return Order String
    */
  def generateOrderForUser(user: String, order: String) = "sudo -u " + user + " DISPLAY=:0.0 " + order

  /**
    * Creates a sudo command, capable of being executed with user privileges on Aton's SSH pipeline.
    * @param order Order to be formatted
    * @return Formatted super user order String
    */
  def sudofy(order: String) = "sudo -S -p '' -- sh -c '" + order.replace("$", "\\$").replace("'", "\"") + "'"
}
