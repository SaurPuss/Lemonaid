Lemonaid for spigot 1.13.2


User wrapper:
    In order to allow mutes and other moderation tools to function players are wrapped and stored
    in a map once they join the server. The wrapper is removed from the map and stored once they
    quit or are kicked. Saving this data will require you to set up a connection with an MySQL
    database, which you can configure in the config.yml which is created the first time this plugin
    is launched.

Permission nodes:
    There are a few pre-defined inheritance groups set up in the Lemonaid permissions, which
    should make it easier to manage the moderation for non-operators. The main one is the
    lemonaid.moderation permission.
    The lemonaid.exempt permission will exempt a player from moderation tools and inherit the
    notification permission. This group does not give the ability to use any of the moderation
    commands.
    The lemonaid.notification permission defaults to false, unless inherited from either of the
    nodes listed above. Of course operators will override this setting and will always be
    notified.
    The option to reload the config files for this plugin is locked behind the lemonaid.reload
    permission, which is set to false by default.
    You can control who is allowed to use chat colors with the lemonaid.chat.color permission,
    which defaults to false for non-operators and players who don't have the lemonaid.moderation
    node.

Moderation commands:
    Broadcast:
      Available to operators and players with the lemonaid.broadcast permission. Use this to
      broadcast a message to all online players and the console. For example; "/broadcast This is a
      broadcast" will result in the server chat displaying "[BROADCAST] This is a broadcast", with
      appropriately flashy colors. Using Minecraft color codes is optional with the use of '&'.
    Mute:
      Available to operators and players with the lemonaid.mute permission. When this command is used
      on a player, a timestamp will be attached to the player wrapper that is evaluated when a player
      tries to chat. As long as the timestamp has not yet expired, the player chat event will be
      cancelled.
      Every time a mute is executed a log is created and saved. The last 10 instances of this log can
      be retrieved by using "/mute recap". A log will have the date of the mute as well as the executor
      of the command embedded for easy reviewing purposes.
      There are a few ways to use the mute command, with the most simple version being "/mute player".
      This will attach a duration timestamp of FOREVER to the player, muting them indefinitely. The
      same version of the command can be used to toggle their timestamp back to 0, automatically lifting
      their mute status.
      The next option is to add a time unit to the command, ie: "/mute player 0", in which case 0 will
      be translated to FOREVER. You also have the option to be more specific by combining a number and
      a letter, such as "/mute player 10m", which will add a timestamp of 10 MINUTES into the future to
      the affected player. So the formula is "number + letter", with the letter options translating as
      such: s = SECONDS, m = MINUTES, h = HOURS, d = DAYS, and y = YEARS.
      The final addition to the command is the ability to add a reason for the mute. Any string of words
      after a valid mute command with a time unit will work. So using "/mute albert 10m uR a NeRd" will
      mute albert for 10 minutes while logging "uR a NeRd" in the recap.
      Players that have the lemonaid.exempt permission cannot be muted, and the command will abort if
      this is attempted.
    Cuff:
      Cuff is accessible to players with the lemonaid.cuff node and will not work on players with the
      lemonaid.exempt permission. The cuff action prevents a player from both moving and chatting,
      making it an easy switch to flip when dealing with hackers and the like. Be careful; unlike
      mute there is no expiration attached. It's simply a status you can toggle on or off.
      All cuff actions are logged in a file and the last 10 entries can be retrieved by using
      "/cuff recap". Each log will automatically register the command executor as well as the date.
    Fly:
      You know how this works; toggle fly on yourself or a target as long as you have the permission
      lemonaid.fly it will work.
    Recap:
      Available to holders of the lemonaid.recap permission node, it allows you to leave short messages
      in a log for important moderation events. Or use it to send jokes to an admin in another time zone.
      It's up to you.
      Usage is simple "/recap Your Message Here" will get logged with a date. The last 10 entries can be
      read by simply using "/recap", starting with the most recent recap.




Custom errors:
  UserNotFound:
    The plugin failed to retrieve the record that is automatically created when a player joins!
    This likely means there is a problem with your database connection!