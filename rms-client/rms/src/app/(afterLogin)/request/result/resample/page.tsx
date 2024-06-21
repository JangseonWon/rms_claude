import Title from "@/app/_component/Title";
import style from "@/app/(afterLogin)/request/result/resample/page.module.css";
import ReSampleTable from "@/app/(afterLogin)/request/result/resample/_component/ReSampleTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <section className={style.titleContainer}>
                <Title/>
            </section>
            <section className={style.mainContainer}>
                <ReSampleTable/>
            </section>
        </div>
    )
}