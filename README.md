# jcordext <a href="#"><img src="https://img.shields.io/badge/version-1.0.2-brightgreen" alt="Latest version"></a>

jcordext is a Utility Framework for Java Discord Bots which uses the [Javacord](https://github.com/BtoBastian/Javacord) Library and API.

As of right now, jcordext is limited in functionality to a Commands framework as well as a Tasks framework, which allows the bot to periodically execute "tasks" in a loop.

##Installation
We do not support maven/gradle as we have yet to push this project to a repository. However, you can download the <a href="https://github.com/ecxx/jcordext/releases/download/v1.0.2/jcordext-1.0.2.jar">jar<a/> here. Note that Javacord has to be included separately.

##Examples

The following code will create a simple bot with a "ping" command:
```java
// in a file named Main.java
public class Main {
    public static void main(String[] args) {
        String token = "YOUR TOKEN";
        Bot bot = new Bot(PREFIX, token);
        bot.registerExtension(new cog(), "Bot");
    }
} 

// in a separate file named cog.java
public class cog implements Extension {

    @Command(
        name = "ping",
        description = "Ping the bot",
        usage = "!ping"
    )
    public void ping(Context ctx) {
        ctx.send('Pong!');
    }

}
```


In order to add arguments to the command you can simply add parameters to the command method:
```java
public class cog implements Extension {

    @Command(
        name = "ping",
        description = "Ping the bot",
        usage = "!ping argument"
    )
    public void ping(Context ctx, String argument) {
        ctx.send(argument + " was sent!");
    }

}
```


You can also use `User`, `Channel` and `Role` objects in your parameters (jcordext will convert them for you):
```java
public class cog implements Extension {

    @Command(
        name = "ping",
        description = "Ping the bot",
        usage = "!ping argument"
    )
    public void ping(Context ctx, User argument) {
        ctx.send(mention(user) + ", pong!"); // the mention() method is built into the Extension class for convenience.
    }

}
```

The prefix is built in and you do not need to specify the prefix in the `@Command` annotation. When you register the Extension to the Bot, it will automatically add the prefix.


