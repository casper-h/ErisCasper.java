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
package net.dv8tion.jda.client.entities;

import java.util.List;
import net.dv8tion.jda.core.Region;
import net.dv8tion.jda.core.entities.AudioChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;

public interface Call extends AudioChannel {
  Region getRegion();

  boolean isGroupCall();

  CallableChannel getCallableChannel();

  Group getGroup();

  PrivateChannel getPrivateChannel();

  String getMessageId();

  long getMessageIdLong();

  List<CallUser> getRingingUsers();

  List<CallUser> getConnectedUsers();

  List<CallUser> getCallUserHistory();

  List<CallUser> getAllCallUsers();
}
