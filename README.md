# Overview #

Note: This was written in very rushed java, it was mainly for a proof of concept

- Android app that displays a system overlay
- Overlay allows custom xml layout to remain on screen even when app is closed
- It is done using a service and adding a view to the window manager
- Similar functionality to facebooks chatheads
- This app displays additional functionality to fb chatheads, this being that it automatically disables itself when a permission I shown on screen
- Typically, if an overlay is showing, then the user cannot accept a permission and needs to kill the app or disable the overlay to accept the permission
- It is automatically disabled and enabled by using an accessibility service to monitor apps that are open
- This works since permissions in android are similar to the keyboard in the sense that its a legit app, so we can monitor package names of apps when they come into focus using accessibility
- This can also be done using usage stats permission, but it will not be instant and is harder to implement
- This functionality for a screen overlay that can disable itself when it needs to is useful for the following applications:
  * Screen filter
  * Password manager apps such as LastPass
  * Fb chat heads
  * Pip on pre-oreo devices. You would need to use a surface in the xml layout, then stream the video to the surface so video can be played outside the app. This would be reverse compatible for all android versions, while android 8 PIP is ONLY for android 8
  * Weather apps that want to show info outside the app
