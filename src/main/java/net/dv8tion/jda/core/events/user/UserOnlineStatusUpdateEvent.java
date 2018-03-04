/*
 *     Copyright 2015-2018 Austin Keener & Michael Ritter & Florian Spieß
 *     Copyright 2018-2018 "Princess" Lana Samson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.core.events.user;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

/**
 * <b><u>UserOnlineStatusUpdateEvent</u></b><br>
 * Fired if the {@link OnlineStatus OnlineStatus} of a {@link net.dv8tion.jda.core.entities.User
 * User} changes.<br>
 * <br>
 * Use: Retrieve the User who's status changed and their previous status.
 */
public class UserOnlineStatusUpdateEvent extends GenericUserPresenceEvent {
  protected final OnlineStatus previousOnlineStatus;

  public UserOnlineStatusUpdateEvent(
      JDA api, long responseNumber, User user, Guild guild, OnlineStatus previousOnlineStatus) {
    super(api, responseNumber, user, guild);
    this.previousOnlineStatus = previousOnlineStatus;
  }

  public OnlineStatus getPreviousOnlineStatus() {
    return previousOnlineStatus;
  }

  public OnlineStatus getCurrentOnlineStatus() {
    return isRelationshipUpdate() ? getFriend().getOnlineStatus() : getMember().getOnlineStatus();
  }
}