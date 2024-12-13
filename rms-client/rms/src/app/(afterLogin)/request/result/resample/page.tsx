import Title from "@/app/_component/Title";
import style from "@/css/requestMainPage.module.css";
import ReSampleTable from "@/app/(afterLogin)/request/result/resample/_component/ReSampleTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.title}>
                <Title/>
            </div>
            <div className={style.contents}>
                <ReSampleTable/>
            </div>
        </div>
    )
}