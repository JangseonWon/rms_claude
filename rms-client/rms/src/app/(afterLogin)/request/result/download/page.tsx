import Title from "@/app/_component/Title";
import style from "@/css/requestMainPage.module.css";
import DownloadTable from "@/app/(afterLogin)/request/result/download/_component/DownloadTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.title}>
                <Title/>
            </div>
            <div className={style.contents}>
                <DownloadTable/>
            </div>
        </div>
    )
}