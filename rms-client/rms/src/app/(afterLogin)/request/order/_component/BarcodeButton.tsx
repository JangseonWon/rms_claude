import style from './barcodeButton.module.css';
import type { RequestWithSelected } from "@/app/(afterLogin)/request/order/_component/OrderTable";

interface Props {
    selectRequest: RequestWithSelected[];
}

export default function BarcodeButton({selectRequest}: Props) {
    const handlePrintClick = () => {
        if (selectRequest.length === 0) {
            alert("No selected.");
            return;
        }
        alert(JSON.stringify(selectRequest));
    };

    return (
        <div className={style.container}>
            <button className={style.barcodeButton} onClick={handlePrintClick}>
                Print Barcode
            </button>
        </div>
    )
}