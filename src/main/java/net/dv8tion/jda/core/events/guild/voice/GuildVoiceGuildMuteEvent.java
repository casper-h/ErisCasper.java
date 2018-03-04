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
package net.dv8tion.jda.core.events.guild.voice;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;

public class GuildVoiceGuildMuteEvent extends GenericGuildVoiceEvent {
  protected final boolean guildMuted;

  public GuildVoiceGuildMuteEvent(JDA api, long responseNumber, Member member) {
    super(api, responseNumber, member);
    this.guildMuted = member.getVoiceState().isGuildMuted();
  }

  public boolean isGuildMuted() {
    return guildMuted;
  }
}