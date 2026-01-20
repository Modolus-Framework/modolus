# Quick start

## Installation

The first step is to add the framework to your plugin.

::: code-group

```groovy [build.gradle]
compileOnly 'dev.modolus:modolus-core:0.0.1'
annotationProcessor 'dev.modolus:modolus-processor:0.0.1'
```

```kotlin [build.gradle.kts]
compileOnly("dev.modolus:modolus-core:0.0.1")
annotationProcessor("dev.modolus:modolus-core:0.0.1")
```

```xml [pom.xml]

<dependencies>
    <dependency>
        <groupId>dev.modolus</groupId>
        <artifactId>modolus-core</artifactId>
        <version>0.0.1</version>
    </dependency>
</dependencies>

<plugins>
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>dev.modolus</groupId>
                <artifactId>modolus-processor</artifactId>
                <version>0.0.1</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
</plugins>
```

:::

## Create the plugin main class

After the installation is done, the main plugin class can be created or refactored, so that it uses the framework.

First step is that your plugin's class extends the BasePlugin and declares a scope, which is needed that the framework
can register singletons for your plugin.

```java

@Scope
public class MyPlugin extends BasePlugin {

    public MyPlugin(@NotNull JavaPluginInit init) {
        super(init);
    }

}
```

To initialize the framework and destroy it afterward the `setup` and `shutdown` methods needs to be overwritten. Then
the `setupPlugin` and `shutdownPlugin` methods needs to be called. They have to be the first call you do, because else
other framework auto-initializer do not have any data.

```java

@Override
protected void setup() {
    setupPlugin();
}

@Override
protected void shutdown() {
    shutdownPlugin();
}
```

Now you got two options, to create the manifest, so that Hytale can detect the plugin, either manually create it as a
file in the resources or add the `@HytalePlugin` annotation, which generates the manifest automatically, while
compilation.

The following example, shows how to use the annotation with manual versioning:

```java{16-19}
@HytalePlugin(
    group = "org.example",
    name = "MyPlugin",
    version = "0.0.1",
    description = "My test plugin",
    authors = {
        @PluginAuthor(
            name = "Test",
            email = "test@example.org",
            url = "https://example.org"
        )
    },
    website = "https://example.org",
    dependencies = {
        // The dependency on modolus-core is required
        @PluginDependency(
            name = "dev.modolus:modolus-core",
            version = "*"
        )
    }
)
```

## Automatic plugin version

Alternatively you can set the plugin version via your build tool, by removing the version in the annotation. For that to
work additional configuration in the build tool is needed like the following:

::: code-group

```groovy [build.gradle]
tasks.compileJava {
    options.compilerArgs += [
        "-AprojectVersion=${version}"
    ]
}
```

```kotlin [build.gradle.kts]
tasks.compileJava {
    options.compilerArgs.add(
        "-AprojectVersion=$version"
    )
}
```

```maven [pom.xml]
<plugins>
    <plugin>
        <artifactId>maven-compiler-plugin</artifactId>
        <configuration>
            <annotationProcessorPaths>
                <path>
                    <groupId>dev.modolus</groupId>
                    <artifactId>modolus-processor</artifactId>
                    <version>0.0.1</version>
                </path>
            </annotationProcessorPaths>
            <annotationProcessorOptions>
                <projectVersion>${project.version}</projectVersion>
            </annotationProcessorOptions>
        </configuration>
    </plugin>
</plugins>
```

:::
