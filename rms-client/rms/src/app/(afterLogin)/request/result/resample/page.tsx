import ServiceTitle from "@/app/_component/ServiceTitle";
import style from "@/app/(afterLogin)/request/result/resample/page.module.css";
import ReSampleTable from "@/app/(afterLogin)/request/result/resample/_component/ReSampleTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <section className={style.titleContainer}>
                <ServiceTitle/>
            </section>
            <section className={style.mainContainer}>
                <ReSampleTable/>
            </section>
        </div>
    )
}