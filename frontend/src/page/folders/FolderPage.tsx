import {
  BookOutlined,
  FolderAddOutlined,
  FolderOutlined,
  PlusOutlined,
} from "@ant-design/icons";
import { Button, Dropdown, Flex } from "antd";
import { useState } from "react";
import { useNavigate } from "react-router";

import { useApi } from "#/api/apiContext.ts";
import { EndlessScrollGrid } from "#/common/components/EndlessScrollGrid.tsx";
import { MediaCard } from "#/common/components/MediaCard.tsx";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { useEffectOnce } from "#/common/hooks/useEffectOnce.ts";
import { useImageUrls } from "#/common/hooks/useImageUrls.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import type { FolderUiDTO } from "#/generated/interface-api.ts";
import { FolderCard } from "#/page/folders/components/FolderCard.tsx";
import { useFolderActions } from "##/src/page/folders/hooks/useFolderActions.tsx";

export const FolderPage = () => {
  const api = useApi();
  const notify = useNotification();
  const navigate = useNavigate();
  const getImageUrl = useImageUrls();

  // --- Folder State ---
  const [folders, setFolders] = useState<FolderUiDTO[]>([]);

  const [fetchFolders, folderPage, loading, loadError] = useMutation(
    (page: number) => api.folder.getAll({ page, size: 20 }),
  );
  const hasMore = !!folderPage && folderPage.page + 1 < folderPage.totalPages;

  // --- Folder Loading ---
  const notifyLoadError = (error: Error) =>
    notify.error({
      title: "Die Ordner konnten nicht geladen werden",
      description: error.message,
    });

  const reloadFolders = () =>
    fetchFolders(0)
      .then((result) => setFolders(result.content))
      .catch(notifyLoadError);

  const loadMoreFolders = () =>
    fetchFolders(folderPage!.page + 1)
      .then((result) =>
        setFolders((previous) => [...previous, ...result.content]),
      )
      .catch(notifyLoadError);

  useEffectOnce(() => {
    reloadFolders();
  });

  // --- Folder Actions ---
  const folderActions = useFolderActions({ onChanged: reloadFolders });

  // --- Render ---
  return (
    <Flex vertical style={{ position: "absolute", inset: 0 }}>
      <SectionHeader
        title={"Meine Rezepte"}
        extra={
          <Dropdown
            placement={"bottomRight"}
            trigger={["click"]}
            menu={{
              items: [
                {
                  key: "folder",
                  icon: <FolderAddOutlined />,
                  label: "Neuer Ordner",
                  onClick: folderActions.openCreate,
                },
                {
                  key: "recipe",
                  icon: <BookOutlined />,
                  label: "Neues Rezept",
                  onClick: () => navigate("/recipe-creation"),
                },
              ],
            }}
          >
            <Button type={"primary"} icon={<PlusOutlined />}>
              Neu
            </Button>
          </Dropdown>
        }
      />

      <EndlessScrollGrid
        loading={loading}
        hasMore={hasMore && !loadError} // TODO JPI: error handling, eg. retry button
        onLoadMore={loadMoreFolders}
        emptyDescription={"Keine Ordner vorhanden"}
      >
        <MediaCard
          title={"Alle deine Rezepte"}
          meta={[{ text: "Alle Rezepte", icon: <FolderOutlined /> }]}
          onClick={() => navigate("/recipe-search")}
        />
        {folders.map((folder) => (
          <FolderCard
            key={folder.id}
            folder={folder}
            imageUrl={getImageUrl(folder.imageKey)}
            onOpen={(folder) =>
              navigate(`/recipe-search`, {
                state: { filter: { folderId: folder.id } },
              })
            }
            onEdit={folderActions.openEdit}
            onDelete={folderActions.confirmDelete}
          />
        ))}
      </EndlessScrollGrid>

      {/* TODO JPI: Better approach? Feels hacky */}
      {folderActions.Modal}
    </Flex>
  );
};
