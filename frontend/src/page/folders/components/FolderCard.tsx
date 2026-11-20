import {
  DeleteOutlined,
  EditOutlined,
  FolderOutlined,
  MoreOutlined,
} from "@ant-design/icons";

import { MediaCard } from "#/common/components/MediaCard.tsx";
import type { FolderUiDTO } from "#/generated/interface-api.ts";

export type FolderCardProps = {
  folder: FolderUiDTO;
  imageUrl?: string;
  onOpen?: (folder: FolderUiDTO) => void;
  onEdit?: (folder: FolderUiDTO) => void;
  onDelete?: (folder: FolderUiDTO) => void;
};

export const FolderCard = ({
  folder,
  imageUrl,
  onOpen,
  onEdit,
  onDelete,
}: FolderCardProps) => (
  <MediaCard
    title={folder.name}
    imageUrl={imageUrl}
    meta={[{ text: "Ordner", icon: <FolderOutlined /> }]}
    onClick={() => onOpen?.(folder)}
    actions={[
      {
        key: "more",
        icon: <MoreOutlined />,
        menu: [
          {
            key: "edit",
            icon: <EditOutlined />,
            label: "Bearbeiten",
            onClick: () => onEdit?.(folder),
          },
          {
            key: "delete",
            icon: <DeleteOutlined />,
            label: "Löschen",
            danger: true,
            onClick: () => onDelete?.(folder),
          },
        ],
      },
    ]}
  />
);
