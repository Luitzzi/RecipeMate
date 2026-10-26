package de.example.backend.service.ai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;

import de.example.backend.boundary.uidto.chat.CurrentViewUiDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CurrentViewAdvisor implements BaseAdvisor {

    public static final String CURRENT_VIEW = "current-view";

    private int order = 0;

    public CurrentViewAdvisor setOrder(int order) {
        this.order = order;
        return this;
    }

    private CurrentViewUiDTO getCurrentView(ChatClientRequest request) {
        Object obj = request.context().get(CURRENT_VIEW);
        if (obj instanceof CurrentViewUiDTO currentView) {
            return currentView;
        }
        throw new RuntimeException("CurrentViewUiDTO not found in request context");
    }

    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
        String userText = request.prompt().getUserMessage().getText();

        CurrentViewUiDTO currentView = getCurrentView(request);
        String currentViewPrefix = "<current_view>" + currentView.describe() + "</current_view>\n";
        String augmentedUserText = currentViewPrefix + userText;

        return request.mutate()
                .prompt(request.prompt().augmentUserMessage(augmentedUserText))
                .build();
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
