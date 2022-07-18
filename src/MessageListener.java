import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.channel.ChannelManager;
import net.dv8tion.jda.api.managers.channel.attribute.IPermissionContainerManager;
import net.dv8tion.jda.api.requests.restaction.ChannelAction;
import net.dv8tion.jda.internal.requests.restaction.ChannelActionImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageListener extends ListenerAdapter {

    static ArrayList <String> channelNames = new ArrayList<>(); //Names it can randomly pick from to name a new channel
    static String lastMessage = "Channel Name"; //This shouldnt need to be initialized but im doing it just in case
    static int channelCount = 2;

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        //Get the last text message
        if (event.getMessage().getType() != MessageType.CHANNEL_PINNED_ADD && !event.getMessage().getContentRaw().equals("")) lastMessage = event.getMessage().getContentRaw();

        //Get the pins of the channel the message is sent in, check if its a pinned message type, and check if the pinned messages are 50 (max)
        event.getChannel().retrievePinnedMessages().queue(pinnedMessages -> {
            if (event.getMessage().getType() == MessageType.CHANNEL_PINNED_ADD
                    && pinnedMessages.size() == 50){

                //Get a useable channel object
                var channel = (ICategorizableChannel) event.getGuildChannel();

                //Move the channel to another category
                channel.getManager().setParent(event.getGuild().getCategories().get(5)).queue();

                //Deny chatting permissions in the old channel
                channel.upsertPermissionOverride(event.getGuild().getRoleById(808448471564025876L)).setDeny(Permission.MESSAGE_SEND).queue();

                String pickedName = "Channel name"; //This shouldnt need to be initialized but im doing it just in case

                try {
                    Random random = new Random();
                    pickedName = channelNames.get(random.nextInt(channelNames.size())); //Try picking a random element from the list
                } catch (Exception ignored){}

                //Create a channel with a name taken from the various means provided
                event.getGuild().createTextChannel(
                        (!channelNames.isEmpty() //If not empty
                                ? pickedName //Get the randomly picked name
                                : lastMessage.length() < 16 //If empy and last message is shorter than 16
                                ? lastMessage //Get the last message
                                : lastMessage.substring(0, 16)) //If longer than 16 trim it down
                                + channelCount, //And add the channel count to it
                        event.getGuild().getCategories().get(1)).queue(); //Put it in the main category
                channelCount++;
                channelNames.remove(pickedName);
            }
        });

        //Add command
        if (event.getMessage().getContentRaw().startsWith(">>add")){
            if (event.getMessage().getContentRaw().substring(6).length() < 95) { //Making sure users cant add names that are too long
                channelNames.add(event.getMessage().getContentRaw().substring(6));
                event.getMessage().reply("Element added succseffully").queue();
            } else {
                event.getMessage().reply("The name is too long!");
            }
        }
        //Show list command
        if (event.getMessage().getContentRaw().startsWith(">>list")){
            String list = "";
            for (int i = 0; i < channelNames.size(); i++) { //Not a for each loop to format more easily
                list += i + ": " + channelNames.get(i) + "\n";
            }
            try {
                event.getMessage().reply(list).queue(); //Try sending the list
            } catch (Exception e){
                event.getMessage().reply("The list is empty").queue(); //In case the list is empty
            }
        }
        //Remove element command
        if (event.getMessage().getContentRaw().startsWith(">>remove")){
            try {
                //Remove the element in integer posittion of the number at substring 9
                channelNames.remove(Integer.parseInt(event.getMessage().getContentRaw().substring(9)));
                event.getMessage().getGuildChannel().sendMessage("Element removed successfully").queue();
            } catch (Exception e){
                event.getMessage().getChannel().sendMessage("Something went wrong, make sure the position you chose is not out of the list's bounds, and that it is of integer type (no letters or spaces after it). Syntax: [>>remove 3]").queue();
            }
        }
    }
}
