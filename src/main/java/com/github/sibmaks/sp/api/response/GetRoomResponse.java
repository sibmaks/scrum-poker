package com.github.sibmaks.sp.api.response;

import com.github.sibmaks.sp.api.entity.GroupInfo;
import com.github.sibmaks.sp.api.entity.ParticipantInfo;
import com.github.sibmaks.sp.api.entity.RoomInfo;
import com.github.sibmaks.sp.exception.NotAllowedException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import com.github.sibmaks.sp.domain.Participant;
import com.github.sibmaks.sp.domain.Role;
import com.github.sibmaks.sp.domain.Room;
import com.github.sibmaks.sp.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Get room response
 *
 * @author sibmaks
 * Created at 12-10-2021
 */
@EqualsAndHashCode(callSuper = true)
public class GetRoomResponse extends StandardResponse {
    @Getter
    private final RoomInfo roomInfo;

    public GetRoomResponse(User user, Room room, List<Participant> participants) {
        List<GroupInfo> groupInfos = participants.stream()
                .collect(Collectors.groupingBy(Participant::getRole, Collectors.toList()))
                .entrySet().stream()
                .map(it -> buildGroupInfo(it, room.isVoting()))
                .collect(Collectors.toList());

        var participant = participants.stream()
                .filter(it -> it.getParticipantId().getUser().getId() == user.getId())
                .findFirst()
                .orElseThrow(NotAllowedException::new);

        this.roomInfo = RoomInfo.builder()
                .id(room.getId())
                .name(room.getName())
                .voting(room.isVoting())
                .score(participant.getScore())
                .author(user.getId() == room.getAuthor().getId())
                .groupInfos(groupInfos)
                .build();
    }

    private GroupInfo buildGroupInfo(Map.Entry<Role, List<Participant>> entry, boolean voting) {
        var groupInfo = new GroupInfo();
        groupInfo.setName(entry.getKey().getName());
        groupInfo.setParticipantInfos(new ArrayList<>());
        for (Participant participant : entry.getValue()) {
            var user = participant.getParticipantId().getUser();
            var participantInfo = ParticipantInfo.builder()
                    .id(user.getId())
                    .name(user.getLastName() + " " + user.getFirstName())
                    .voted(participant.getScore() != null)
                    .score(voting ? null : participant.getScore())
                    .build();
            groupInfo.getParticipantInfos().add(participantInfo);
        }

        return groupInfo;
    }
}
