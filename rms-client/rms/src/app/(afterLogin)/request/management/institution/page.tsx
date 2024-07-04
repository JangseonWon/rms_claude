import style from '@/app/(afterLogin)/request/management/institution/page.module.css'
import InstitutionTable from "@/app/(afterLogin)/request/management/institution/_component/InstitutionTable";

export default async function Page() {
    return(
        <div className={style.container}>
            <InstitutionTable/>
        </div>
    )
}