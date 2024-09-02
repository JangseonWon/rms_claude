import style from '@/app/(afterLogin)/request/management/service/page.module.css'
import ServiceManageTable from "@/app/(afterLogin)/request/management/service/_component/ServiceManageTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <ServiceManageTable/>
        </div>
    )
}