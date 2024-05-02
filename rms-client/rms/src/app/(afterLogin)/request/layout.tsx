import React, {ReactNode} from "react";
import NavMenu from "@/app/(afterLogin)/request/_component/NavMenu";
import style from "@/app/(afterLogin)/request/layout.module.css"
import Footer from "@/app/_component/Footer";

type Props = { children: ReactNode};
export default async function Layout({ children }: Props) {
    return (
        <>
            <div className={style.container}>
                <section className={style.leftSection}>
                    <NavMenu/>
                </section>
                <section className={style.rightSection}>
                    {children}
                </section>
            </div>
            <Footer/>
        </>
    )
}
