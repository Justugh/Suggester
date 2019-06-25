## 📊 What is Suggester?
Suggester is a Discord bot written in Java using the JDA api. It's purpose is to allow your
community to provide you with suggestions in a neat and useful way.

## 👨‍💻 Commands
All commands start with `>` by default, this can be modified using the config command.

The commands `accept` and `reject` require you to run the command in a suggestion channel
or in it's linked channel. If the command is ran in an invalid channel it will fallback
to the default suggestion channel.

| Command | Description | Default Permission |
| --------------- | ---------------- | ---------------- |
| \>help | Display help information. | NONE
| \>suggest [suggestion] | Send a suggestion. | NONE
| \>user [user] | View user information. | ADMINISTRATOR
| \>accept [message ID] | Accept a suggestion. | MESSAGE_MANAGE
| \>reject [message ID] | Reject a suggestion. | MESSAGE_MANAGE

### Config Commands
All config commands by default require the `ADMINISTRATOR` permission.

| Command | Description |
| --------------- | ---------------- |
| \>config display | Display the configuration values. | 
| \>config permissions | Display command permissions. |
| \>config prefix [prefix] | Set the command prefix. |
| \>config linked [channel] | View the channels linked channel. |
| \>config default-channel [channel] | Set the default suggestion channel. |
| \>config reaction-permission [permission] | Set the reaction accept/reject permission. |
| \>config link-channel [channel] [channel] | Link a channel to another channel. |
| \>config permission [command] [permission] | Change a commands permission. |

## 💬 Suggestions
If you have any suggestions to add to the bot you can create issue on github
or join the bot's official Discord server!

![https://discord.gg/5CYFzA7](https://cdn0.iconfinder.com/data/icons/free-social-media-set/24/discord-128.png)