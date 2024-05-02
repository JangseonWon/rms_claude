import React from "react";
import style from "@/app/(afterLogin)/request/dashboard/page.module.css"
import Statistics from "@/app/(afterLogin)/request/dashboard/_component/Statistics";
import Table from "./_component/Table";

export default async function Page() {
    return(
        <>
            <div className={style.container}>
                <div className={style.statisticsSection}>
                    <Statistics/>
                </div>
                <Table/>
            </div>

        </>
    )
}