# Modolus

Modolus is a framework which intents to make modding in hytale easier
by providing useful tools such as code generation and more.

## Quick start

Add the modolus framework to your project.

```groovy
// tbd
```

Create your plugin class like the following.

```java
@HytalePlugin(
        group = "org.example",
        name = "MyPlugin",
        description = "My first plugin!",
        authors = {
                @PluginAuthor(
                        name = "Me",
                        email = "me@example.org",
                        url = "https://example.org"
                )
        },
        website = "https://example.org",
        dependencies = {
                @PluginDependency(
                        name = "com.modolus:modolus-core",
                        version = "*"
                )
        }
)
@Scope
public class MyPlugin extends BasePlugin {
    
    public MyPlugin(@NotNull JavaPluginInit init) {
        super(init);
    }
    
    @Override
    protected void setup() {
        setupPlugin();
        registerCommands();
        registerEventListeners();
    }
    
    @Override
    protected void shutdown() {
        shutdownPlugin();
    }
    
}
```

Add a command like this:

```java
@Command(
        name = "test",
        description = "Test"
)
@ProvideSingleton(AbstractCommand.class)
@CreateOnRuntime
public class TestCommand extends AbstractTestCommand {

    @Override
    protected @Nullable CompletableFuture<Void> executeCommand(@NotNull CommandContext commandContext) {
        commandContext.sendMessage(Message.raw("Test"));
        return null;
    }
}
```

