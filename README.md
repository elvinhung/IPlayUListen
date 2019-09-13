# IPlayUListen

## Description
This is a desktop app that allows users to listen to music with each other. It comes with a chat room where users can interact with one another and a song library with music to choose from. There is a "host" for each server that controls what song is played and has the ability to play/pause/skip the song, hence the name IPlayUListen (based off one of my favorite ODESZA songs).

When a song is chosen by the "host", the audio file is streamed using TCP from the server to all connected clients. The chat room is also implemented using TCP. If you want to try using this yourself, make sure ports 8080 and 8081 are available.

## Images

### Login Screen + Login Error

![login1](https://drive.google.com/uc?export=view&id=1e3Rc5nET3tHr786V54PlY_qGnqAbVSZf)
![login2](https://drive.google.com/uc?export=view&id=1mdiZdYtYm1nlrJYynLDg1hQz66SayB2D)
![login error](https://drive.google.com/uc?export=view&id=1f9CBO_wYAiCYNcLmYvcXVI6gx7A3xR2Q)

### Host Client with playback controls and enabled song library

![host client1](https://drive.google.com/uc?export=view&id=1J8_n2pMEz2O6LN317v24kWGOSOXe1RbI)
![host_client2](https://drive.google.com/uc?export=view&id=1nLQmOMgB6V4HBQw0eOKiXT8V6rAb031y)
![host client selection](https://drive.google.com/uc?export=view&id=1zQt6lJpE90jTCDutAiHLExhlFNu9q6yq)
![host client song playing](https://drive.google.com/uc?export=view&id=1T4DMItBtf8P-pvbZ3APEpbAWXGZcrAi8)

### Non-host client with disabled playback controls and disabled song library

![nonhost client1](https://drive.google.com/uc?export=view&id=1cNtUIXI1ICdULE8qKiu9GeLekQCRgr-N)
![](https://drive.google.com/open?id=140h0k8U0YDqsTILOHejS9Zx_rfnvQj9Q)
![nonhost client2](https://drive.google.com/uc?export=view&id=140h0k8U0YDqsTILOHejS9Zx_rfnvQj9Q)
