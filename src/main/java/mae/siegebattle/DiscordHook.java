package mae.siegebattle;


import org.bukkit.entity.Player;

public class DiscordHook {
    private String webhook;

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public void sendChatWebhook(String message, String uuid, String name){
        String formatted_message = message.replaceAll("\\u00A7[\\s\\S]", "");
        String content = "{\"content\":\"" +
                formatted_message +
                "\"\n,\"avatar_url\":\"https://crafatar.com/avatars/" +
                uuid +
                "\"\n,\"username\":\"" +
                name
                + "\"}";
        send(content);
    }

    public void sendLoginMessage(Player player){
        String content = "{\"content\":\"" + "**{name}さんがログインしました！**".replace("{name}", player.getName())
                        + "\"\n,\"avatar_url\":\"https://crafatar.com/avatars/%1$s\"".replace("%1$s", player.getUniqueId().toString()) +
                        "\n,\"username\":\"{username}\"}".replace("{username}", player.getName());
        send(content);
    }

    public void sendLeaveMessage(Player player){
        String content = "{\"content\":\"" + "{name}さんがログアウトしました。".replace("{name}", player.getName())
                + "\"\n,\"avatar_url\":\"https://crafatar.com/avatars/%1$s\"".replace("%1$s", player.getUniqueId().toString()) +
                "\n,\"username\":\"{username}\"}".replace("{username}", player.getName());
        send(content);
    }

    public void sendSystemMessage(String text){
        String content = "{\"content\":\"{text}\"\n,\"username\":\"{name}\"}"
                .replace("{name}", "System")
                .replace("{text}", text);
        send(content);
    }


    void send(String text){
        new Thread(() -> NetClient.post(webhook, text)).start();
    }
}
