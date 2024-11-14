import React, {ReactNode} from "react";
import '@fortawesome/fontawesome-svg-core/styles.css';
import {config} from '@fortawesome/fontawesome-svg-core';
import style from "@/css/managementPage.module.css";
import Title from "@/app/_component/Title";


config.autoAddCss = false;

type Props = { children: ReactNode};
export default async function Layout({ children }: Props) {
    return (
        <div className={style.container}>
            <div className={style.title}>
                <Title/>
            </div>
            <div className={style.contents}>
                {children}
            </div>
        </div>
    )
}
