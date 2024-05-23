import style from "@/app/(afterLogin)/user/_component/institutions.module.css";
import * as React from "react";
import InstitutionTable from "@/app/(afterLogin)/user/_component/InstitutionTable";

export default function Institutions() {
    return (
        <div className={style.container}>
            <div className={style.header}>
                Institutions
            </div>
            <section className={style.table}>
                <InstitutionTable/>
            </section>
        </div>
    );
}