# 🐄 Kuhfu[ss/ß]
> **Kuhfuss** is a Java agent that can attach itself to a Minecraft instance and brute-force password-protected chests 
from the [SecurityCraft mod](https://www.curseforge.com/minecraft/mc-mods/security-craft).

## Demo
https://github.com/user-attachments/assets/44b5c696-a07d-4ec1-9f24-321f64f55409

## Features
 - 🔐 Cracks codes automatically in the background without any annoying GUIs blocking your vision. No need to stop what
you're doing or even stay near the target chest; brute-forcing works through walls and at any range.
 - ⏳️ Automatically adjusts the packet frequency based on lag and server 
limits to maximize the number of attempts a second.
- 💉 Being an agent, Kuhfuss does not really require an installation in the traditional sense. It can be attached by 
simply double-clicking the jar-file. Once you're done you can revert all changes made to the game to prevent crashes 
without even restarting your game.

## Limitations
- **Code-frequency**: Starting with SecurityCraft version v1.9.10, the server imposes a limit on the amount of time that must elapse between 
two password attempts. By default, this value is 250 milliseconds, but in practice it is slightly higher, especially if
the chunk containing the chest is not actively being loaded. The maximum speed of the brute-force attack will therefore
vary from server to server.
- **Cracking far away chests**: To determine whether the chest has been cracked, the agent intercepts all packets 
instructing the client to open a container. It then checks whether the player could have manually entered the code 
and whether the title matches that of the target chest. If the target chest is too far away, the request to determine
its name will fail, and Kuhfuss must fall back on the default values for SecurityCraft's single and double chests.
If the target chest was renamed in an anvil beforehand but the default values were assumed, even the correct code 
won’t trigger a crack message. To avoid this, you can either write down the target chest’s name somewhere and pass 
it along with the brute-force command, or-to be on the safe side-make sure you’re near the chest every time you
start the game.
- **Brute-forcing won't work while any screens are open in game**. That's because only one screen can be opened at a time,
so even if were to hit the right combination the server wouldn't send the request to open the chest. To prevent this
brute-forcing is paused whenever you open up a screen (indicated by code above hotbar being in brackets)
and automatically resumes when you close it. 

## Protecting yourself against brute-force attacks
**This tool was developed solely as a proof of concept for an old idea and was never intended for actual use against
other players. Here's how you can defend yourself against brute-force attacks:**

- **For server owners:**
Consider increasing the value of `passcode_check_cooldown` in your `securitycraft-server.toml` file from 250 to 
1000-5000. For regular players, this is unlikely to make much of a difference, but the time required to crack a single 
passcode increases linearly with the cooldown (250 ms → 1000 ms quadruples the time required).

- **For regular players:**
For a code with up to four digits, there are 10^4+10^3+10^2+10^1 = 11,110 possible combinations.
At a maximum speed of 250 ms between requests, this means that all four-digit passcodes can be tried in about 47 
minutes. If you choose a five-digit passcode, the time increases to about 7.5 hours; for six digits, about 77 hours, 
and so on. So try to choose passcodes that are at least 6 digits long, and ideally, don’t reuse passcodes, so that a 
single compromise doesn’t expose all your chests.

# Supported versions and launchers
- **This program was built specifically for NeoForge version 21.8.53 (Minecraft version 1.21.8) and SecurityCraft version
1.10.1, but that doesn't mean that it won't work for your game version. Attaching itself to the game is achieved by
searching for specific class names and functions, which may - or may not - have had different names in the past and are 
subject to change in the future. For that reason I can't really compile a list of all supported game and mod versions.
Just try out Kuhfuss to see if it works for your setup, and if it doesn't, feel free to open an issue here on GitHub 
so we can work together to find a solution.**
If you're tech-savvy and want to fix that stuff by yourself: SecurityCraft itself didn't make that many changes to the
way that passcodes work but whenever Minecraft renames its functions there is a chance that stuff will break. 
This also applies when using a different mod loader than NeoForge, since the obfuscation mappings used are different. 
To fix the issues, simply update the affected strings in the `ReflectionHelper.java` and the targets in the transformers
with the correct mappings that you can obtain at [Linkie's mappings section](https://linkie.shedaniel.dev/mappings).
- In theory, all launchers work, but only Prism Launcher will work out of the box.
When attaching itself to a running Java program the name of the main-class is used to identify your Minecraft instance.
Different launchers may use different main-class names and thus require some further configuration on your side. If no 
game instances are found when running the jar, figure out which name disappears when you close your game. Then, open up
the jar using your preferred file archiver and add that class-name as a new line in the `main-classes.txt` file. 
Once you've figured out the name of the new main class, please open a new issue on GitHub with it, or open a pull
request with the edited file to improve overall compatibility.

## Usage
Before you use Kuhfuss, you should read the sections on limitations and supported versions.
1.Download the latest jar-file from the releases section
2.To attach the program to your game, choose your preferred method from the two options below
- **Attaching the agent to a running Minecraft instance:**
  > To attach the agent to a game that's already running, you'll need a Java JDK, since the Attach API isn't
  available in the standard JRE.

  Simply run the JAR file, and the rest will be handled for you. Keep in mind that newer versions of Java may
  disallow the dynamic loading of agents unless the `-XX:+EnableDynamicAgentLoading` JVM startup parameter is explicitly
  set. If you run into any problems, fall back to static attaching.
- **Attaching the agent statically**:

  Depending on which launcher you use to start the game, there may be a built-in feature for attaching agents.
  In the Prism launcher, for example, you'll find this feature as shown in the screenshot.
  ![Right-click on the instance you want to attach to and select edit. Then, under versions you'll find an option to "Add Agents" ](https://github.com/user-attachments/assets/e15b3fca-2706-4ee8-83f1-a72812b263ce)
  If your preferred launcher doesn't have this feature, you can manually attach the agent by adding the
  startup parameter `-javaagent:{Path_to_your_jar}` to your launcher.
3. If the program has been successfully attached, you should see text above the hotbar indicating the program's current
   status. Type `.help` in the chat to see all available commands, or watch the video demonstration above. Any messages
   that begin with a period will be blocked and will not be visible to other players.
## Credits
This project uses [Javassist](https://github.com/jboss-javassist/javassist) to inject custom code into the game.
