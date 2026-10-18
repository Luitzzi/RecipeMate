package de.example.backend.boundary.uidto.chat;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reference to an application resource (e.g. a recipe) that a {@link ChatUserMessageUiDTO} relates to.
 * Only type and id are transmitted; the backend loads the actual data.
 *
 * @see ChatContextType
 */
@Getter
@Setter
@NoArgsConstructor
public class ChatContext {

    private ChatContextType type;
    
    private UUID id;

}
