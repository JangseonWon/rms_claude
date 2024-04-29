import React, {ReactNode} from "react";
import style from "@/app/(beforeLogin)/layout.module.css";
import Header from "@/app/(beforeLogin)/_component/Header";
import Footer from "@/app/(beforeLogin)/_component/Footer";

type Props = { children: ReactNode};
export default function Layout({ children }: Props) {

    return (
        <div className={style.container}>
            <section className={style.topSection}>
                <Header/>
            </section>
            <section className={style.bodySection}>
                {children}
            </section>
            <section className={style.bottomSection}>
                <Footer/>
            </section>
        </div>
    )
}
