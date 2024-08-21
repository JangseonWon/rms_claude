import ServiceTitle from "@/app/_component/ServiceTitle";
import style from "@/app/(afterLogin)/request/result/download/page.module.css";
import DownloadTable from "@/app/(afterLogin)/request/result/download/_component/DownloadTable";

export default async function Page() {


    return(
        <div className={style.container}>
            <section className={style.titleContainer}>
                <ServiceTitle/>
            </section>
            <section className={style.mainContainer}>
                <DownloadTable/>
            </section>
        </div>
    )
}