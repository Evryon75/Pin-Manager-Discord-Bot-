# Pin-Manager-Discord-Bot-
This bot checks the number of pinned messages in a channel, and if they have reached the limit the channel is locked and archived. A new channel will be created to replace the old one.
> This was made for a small server of friends where we pin funny messages to save them.

# Notes: 
■ I was unaware that the token can be found in the production folder, uploading this made me have to reset the bot's token and restart it.

■ This is version 0. I have had to add features, and optimise so the currently used code wont reflect this one, as ive done the modifications directly on the machine im hosting the bot on.

■ As time went by the bot began slowing down and i blamed it on the JDA at first but it turns out i just had to schedule a task that flushes the dns cache every day.
