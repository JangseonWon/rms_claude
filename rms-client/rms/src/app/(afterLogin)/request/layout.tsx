import {ReactNode} from "react";
import NavMenu from "@/app/(afterLogin)/_component/NavMenu";
import style from "@/app/(afterLogin)/request/layout.module.css"

type Props = {
    children: ReactNode;
};

export default function Layout({ children}: Props) {

    return (
        <div className={style.container}>
            <div className={style.leftSection}>
                <NavMenu/>
            </div>
            <div className={style.rightSection}>
                {children}
            </div>
        </div>
    )
}
