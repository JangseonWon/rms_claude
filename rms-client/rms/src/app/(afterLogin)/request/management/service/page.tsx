import style from '@/app/(afterLogin)/request/management/service/page.module.css'
import ServiceTable from "@/app/(afterLogin)/request/management/service/_component/ServiceTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <ServiceTable/>
        </div>
    )
}