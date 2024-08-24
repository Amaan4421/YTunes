# AudioPlayer
AudioPlayer is an Android application designed for seamless music streaming from YouTube. With a sleek, user-friendly interface, this app provides a rich listening experience, allowing you to easily browse, search, and play your favorite tracks. It also supports playlist management and background playback, ensuring your music experience remains uninterrupted. Whether you're a casual listener or a music enthusiast, AudioPlayer offers a convenient way to enjoy YouTube's vast music library on the go.

# AudioPlayer is an Android application designed for following features:

- Seamless Music Streaming: Stream music directly from YouTube with ease thanks to integration with '[yt-dlp](https://github.com/yt-dlp/yt-dlp)'.
- User-Friendly Interface: Enjoy a sleek and intuitive design for a smooth experience.
- Playlist Management: Create and manage your playlists effortlessly.
- Background Playback: Keep your music playing while you use other apps or lock your device.


# Technical Information

# yt-dlp
AudioPlayer utilizes yt-dlp, a powerful and flexible fork of youtube-dl, for efficient YouTube video and audio extraction. yt-dlp enhances the app’s ability to fetch high-quality audio streams and manage various video formats, improving your overall streaming experience.

# Chaquopy
The app integrates Python code into the Java-based Android application using Chaquopy, version 15.0.0. Chaquopy is a plugin that enables Python and Java interoperability, making it easier to incorporate Python libraries and scripts into your Android project. This setup enhances the app’s functionality and provides a robust solution for complex processing tasks.


# Features

- Stream music directly from YouTube.
- User-friendly interface.
- Playlist management.
- Background playback.


# Installation

- Clone the repo: git clone https://github.com/Amaan4421/AudioPlayer.git
- Open the project in Android Studio.
- Obtain your own YouTube Data API key from the [Google Cloud Console](https://console.cloud.google.com/). 
- Add the API key to your gradle.properties file with the name API_KEY.
- Build and run the app on your device or emulator.


# Usage

- Browse, search, and play music from YouTube.
- Manage your playlists effortlessly.


# How to obtain API key from Google cloud?

- Go to the [Google Cloud Console](https://console.cloud.google.com/).
- Sign in with your Google account if you aren't already logged in.
- Create a new project or select an existing project from the project dropdown at the top.
- Navigate to the API & Services section by clicking on the hamburger menu (three horizontal lines) on the top left, then selecting API & Services > Library.
- In the API Library, search for "YouTube Data API v3" and click on it.
- Click the Enable button to activate the API for your project.
- After enabling the API, go to the Credentials tab on the left sidebar.
- Click on Create Credentials and select API Key from the dropdown.
- Your new API key will be generated and displayed. Copy that key and paste it in gradle.properties file in Android Studio.


  # Project Information

  App Status:
  - The app is now in a stable state and available for users. ![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
    

  Technical Specifications:
  - ![Chaquopy](https://img.shields.io/badge/chaquopy-v15.0.0-orange)  Chaquopy Version: 15.0.0
  - ![Android Version](https://img.shields.io/badge/android%20version-1.0-green)  Android Version: 1.0
  - ![Min SDK](https://img.shields.io/badge/min%20SDK-24-blue)  Min SDK: 24
  - ![Target SDK](https://img.shields.io/badge/target%20SDK-34-blue)  Target SDK: 34


  License:
  - ![License](https://img.shields.io/badge/license-MIT-blue)
  - Chaquopy License: The app uses Chaquopy version 15.0.0, which is licensed under its own terms. For more details, refer to [Chaquopy License Information]    (https://chaquo.com/chaquopy/doc/current/changelog.html).
  - Other Dependencies: All other dependencies and their licenses are managed through Gradle and are subject to their respective terms.


# Contribution 

- Contributions are welcome! Please fork the repo and create a pull request.
