import { App } from "antd";
import { useState } from "react";

import { useApi } from "#/api/apiContext.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import type { FolderUiDTO } from "#/generated/interface-api.ts";
import { FolderModal } from "#/page/folders/components/FolderModal.tsx";

type UseFolderActionsOptions = {
  onChanged: () => void;
};

export const useFolderActions = ({ onChanged }: UseFolderActionsOptions) => {
  const api = useApi();
  const notify = useNotification();
  const { modal } = App.useApp();

  const [open, setOpen] = useState(false);
  const [folder, setFolder] = useState<FolderUiDTO>(); // useState to avoid text flicker when closing
  const editMode = folder !== undefined;

  const [saveFolder, , saving] = useMutation(
    (values: { name: string; imageKey?: string }) =>
      editMode
        ? api.folder.update(folder.id!, { ...folder, ...values })
        : api.folder.create(values),
  );

  const submit = (values: { name: string; imageKey?: string }) =>
    saveFolder(values)
      .then((saved) => {
        notify.success({
          title: editMode
            ? `Ordner ${saved.name} gespeichert`
            : `Ordner ${saved.name} erstellt`,
        });
        setOpen(false);
        onChanged();
      })
      .catch((error: Error) =>
        notify.error({
          title: editMode
            ? "Der Ordner konnte nicht gespeichert werden"
            : "Der Ordner konnte nicht erstellt werden",
          description: error.message,
        }),
      );

  const confirmDelete = (folderToDelete: FolderUiDTO) => {
    modal.confirm({
      title: `Ordner ${folderToDelete.name} löschen?`,
      okText: "Löschen",
      okButtonProps: { danger: true },
      cancelText: "Abbrechen",
      onOk: () =>
        api.folder
          .delete(folderToDelete.id!)
          .then(() => onChanged())
          .catch((error) =>
            notify.error({
              title: "Der Ordner konnte nicht gelöscht werden",
              description: error.message,
            }),
          ),
    });
  };

  const Modal = (
    <FolderModal
      open={open}
      editMode={editMode}
      saving={saving}
      initialFolder={folder}
      onSubmit={submit}
      onCancel={() => setOpen(false)}
    />
  );

  return {
    openCreate: () => {
      setFolder(undefined);
      setOpen(true);
    },
    openEdit: (folderToEdit: FolderUiDTO) => {
      setFolder(folderToEdit);
      setOpen(true);
    },
    confirmDelete,
    Modal,
  };
};
