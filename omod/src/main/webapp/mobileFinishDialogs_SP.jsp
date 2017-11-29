        
        <!-- Initial not-finished dialog. Usually only for adolescent forms -->
        <div id="not_finished_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>No Completado</h1>
            </div>
            <div data-role="content">
                <span>Esta forma no es completa. Por favor complete antes de continuar.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">Cerca</a>
                </div>
            </div>
        </div>
        
         <!-- Final not-finished dialog -->
         <div id="not_finished_final_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>No Completado</h1>
            </div>
            <div data-role="content">
                <span>Esta forma no es completa. Est&aacute;s seguro de que quieres continuar?</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" onclick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">Si</a>
                    <a href="" data-inline="true" data-rel="back" data-role="button" data-theme="b" style="width: 150px;">No</a>
                </div>
            </div>
        </div>
        
        <div id="finish_error_dialog_sp" class="extended-header" data-role="popup" data-dismissible="false" data-theme="b" data-overlay-theme="a" >
            <div data-role="header" data-theme="b">
                <h1>Error</h1>
            </div>
            <div data-role="content">
                <span>Hubo un error al enviar el formulario. Por favor, pulse 'OK' para intentarlo de nuevo.</span>
                <div style="margin: 0 auto;text-align: center;">
                    <a href="" onclick="finishForm()" data-inline="true" data-role="button" data-theme="b" style="width: 150px;">OK</a>
                </div>
            </div>
        </div>