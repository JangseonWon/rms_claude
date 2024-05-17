import {ReactNode} from "react";
import NavMenu from "@/app/(afterLogin)/request/_component/NavMenu";
import style from "@/app/(afterLogin)/request/layout.module.css"

type Props = { children: ReactNode, modal: ReactNode }
export default function Layout({ children, modal }: Props) {
    return (
        <div className={style.container}>
            <div className={style.leftSection}>
                <NavMenu/>
            </div>
            <div className={style.rightSection}>
                {modal}
                {children}
            </div>
        </div>

    )
}
