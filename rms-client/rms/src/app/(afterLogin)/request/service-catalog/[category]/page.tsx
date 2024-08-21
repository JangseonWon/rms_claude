import style from "./page.module.css";
import CatalogTitle from "./_component/CatalogTitle";
import ServiceCard from "@/app/(afterLogin)/request/service-catalog/[category]/_component/ServiceCard";

export default async function Page() {
    return(
        <div className={style.container}>
            <div className={style.orderContainerWrapper}>
                <div className={style.titleContainer}>
                    <CatalogTitle/>
                </div>
                <div className={style.cardContainer}>
                    <ServiceCard/>
                </div>
            </div>
        </div>
    )
}