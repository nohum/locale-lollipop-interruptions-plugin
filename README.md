Interruptions Locale Plugin
===========================

A Locale plugin to modify the Lollipop interruption setting (also known as "zen mode") programatically.
The plugin supports setting the interruption setting to the modes all, priority or none. However, due
to Android API limitations, setting the mode only supports the indefinite time constraint.

Build variants
--------------

Building the plugin using the `pluginOnlyRelease` variant should be enough. The `additionalActivity`
flavor include an extra activity useful for debugging purposes.

License
-------

See [LICENSE](LICENSE).