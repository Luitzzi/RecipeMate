import {
  CheckCircleTwoTone,
  InfoCircleTwoTone,
  WarningTwoTone,
} from "@ant-design/icons";
import { App } from "antd";
import type { ArgsProps } from "antd/es/notification";
import type { ReactNode } from "react";

import { tokens } from "#/theme/tokens.ts";

export type NotificationProps = Omit<ArgsProps, "type"> & {
  title: ReactNode;
  description?: ReactNode;
};

const commonProps = {
  duration: 3,
  pauseOnHover: true,
  showProgress: true,
};

export const useNotification = (): Record<
  "success" | "info" | "warn" | "error",
  (props: NotificationProps) => void
> => {
  const { notification } = App.useApp();

  const success = (props: NotificationProps) => {
    notification.success({
      ...commonProps,
      icon: <CheckCircleTwoTone twoToneColor={tokens.color.success} />,
      ...props,
    });
  };

  const info = (props: NotificationProps) => {
    notification.info({
      ...commonProps,
      icon: <InfoCircleTwoTone twoToneColor={tokens.color.info} />,
      ...props,
    });
  };

  const warn = (props: NotificationProps) => {
    notification.info({
      ...commonProps,
      icon: <WarningTwoTone twoToneColor={tokens.color.warning} />,
      ...props,
    });
  };

  const error = (props: NotificationProps) => {
    notification.error({
      ...commonProps,
      icon: <WarningTwoTone twoToneColor={tokens.color.error} />,
      ...props,
    });
  };

  return {
    success,
    info,
    warn,
    error,
  };
};
