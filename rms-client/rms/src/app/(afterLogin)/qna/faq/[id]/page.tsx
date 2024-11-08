import style from './page.module.css';
import React from "react";
import FaqPage from "@/app/(afterLogin)/qna/faq/[id]/_component/FaqPage";

export default async function Page() {

    return(
        <div className={style.container}>
            <div className={style.innerBody}>
                <FaqPage/>
            </div>
        </div>
    )
}