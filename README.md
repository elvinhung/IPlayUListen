# IPlayUListen

## Description
This is a desktop app that allows users to listen to music with each other. It comes with a chat room where users can interact with one another and a song library with music to choose from. There is a "host" for each server that controls what song is played and has the ability to play/pause/skip the song, hence the name IPlayUListen (based off one of my favorite ODESZA songs).

When a song is chosen by the "host", the audio file is streamed using TCP from the server to all connected clients. The chat room is also implemented using TCP. If you want to try using this yourself, make sure ports 8080 and 8081 are available.
