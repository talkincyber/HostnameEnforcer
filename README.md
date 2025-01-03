# Block Connections

This is a minecraft plugin that's utilized to stop bot scanning activity. Sometimes these bots will post servers to grief or inform otherwise unwanted visitors. The solution to a lot of these problems is to have explicit allowed hostnames that must be used to connect. This plugin is designed such that you can configure one or more allowed hostnames, and anything else will be blocked with a custom block message sent to their client.

```
# Block Connection Config

version: 1 # DO NOT CHANGE. Used to track for future changes.
enabled: true # Enables or disabled the plugin.

# These are configured hostnames, can add or remove by adding additional lines
hostname:
  - mc.example.com
  - mc2.example.com
  - mc3.example.com
port: 25565 # Port that your server listens on
kick-message: Unauthorized # Custom kick message

# Enables logging of blocked ping events (Default: true)
enable-logging: true
```

