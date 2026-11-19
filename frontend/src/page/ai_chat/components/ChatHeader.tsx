import { Button } from "antd";
import { MessageSquarePlus, X } from "lucide-react";

import { tokens } from "#/theme/tokens.ts";

type Props = {
  onNewChat: () => void;
  onClose: () => void;
  newChatDisabled?: boolean;
};

export const ChatHeader = ({ onNewChat, onClose, newChatDisabled }: Props) => (
  <div
    style={{
      display: "flex",
      alignItems: "center",
      justifyContent: "space-between",
    }}
  >
    <span>Mate</span>
    <div style={{ display: "flex", gap: tokens.gap.small }}>
      <Button
        type="text"
        size="small"
        icon={<MessageSquarePlus size={tokens.iconSize.medium} />}
        disabled={newChatDisabled}
        onClick={onNewChat}
        style={{ color: tokens.color.text.onPrimary }}
      />
      <Button
        type="text"
        size="small"
        icon={<X size={tokens.iconSize.medium} />}
        onClick={onClose}
        style={{ color: tokens.color.text.onPrimary }}
      />
    </div>
  </div>
);
