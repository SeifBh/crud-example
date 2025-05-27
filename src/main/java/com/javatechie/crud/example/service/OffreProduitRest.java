package fr.aba.prevoyance.api.rest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.aba.prevoyance.api.component.offre.OffreComponent;
import fr.aba.prevoyance.api.filter.OffreFilter;
import fr.aba.prevoyance.api.parametrage.OffreConstante.ReglesEligibilite;
import fr.aba.prevoyance.api.parametrage.ProduitConstante.Produits;
import com.aviva.prevoyance.pivotparametrage.offre.OffreCriteresEligibilite;
import com.aviva.prevoyance.pivotparametrage.offre.v2.OffreParam;
import com.aviva.prevoyance.pivottechnical.MicroServiceResponse;
import com.aviva.prevoyance.pivottechnical.exceptions.v2.EnumNotFoundException;
import com.aviva.prevoyance.utils.api.component.ApiAuthentComponent;
import com.aviva.prevoyance.utils.api.handler.AEndPointPrevoyanceRestSpring;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(description = "REST API pour la gestion des offres.", tags = { "Offres" })
@RestController
@RequestMapping("/api/offre/produit")
public class OffreProduitRest extends AEndPointPrevoyanceRestSpring {

	@Value("${api.authent.parametrage}") private String apiAuthent;

	@Autowired private ApiAuthentComponent componentApiAuthent;
	@Autowired private OffreComponent component;
	@Autowired private OffreFilter fOffre;

	@ApiOperation(value = "Récuperer les offres d'un produit")
	@GetMapping("/{codeProduit}/v2")
	public ResponseEntity<MicroServiceResponse<List<OffreParam>>> offresV2(@ApiParam(hidden = true) @RequestAttribute("uuid") final UUID uuidTrace, @RequestHeader("app-key") final String appKey,
			@RequestHeader("app-consumers") final String appConsumers, @PathVariable("codeProduit") final String codeProduit) {

		componentApiAuthent.verifyV2(appKey, appConsumers, apiAuthent);

		List<String> erreurs = new ArrayList<>();
		Produits produit = null;
		try {
			produit = Produits.getByCode(codeProduit);
		} catch (EnumNotFoundException e) {
			erreurs.add(e.getMessage());
		}
		if (!erreurs.isEmpty()) {
			return new ResponseEntity<>(new MicroServiceResponse<>(erreurs), HttpStatus.BAD_REQUEST);
		}

		List<OffreParam> offres = component.getOffresProduitV2(produit, null, uuidTrace);
		if (offres.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(new MicroServiceResponse<>(offres), HttpStatus.OK);
	}

	@ApiOperation(value = "Récuperer les offres commercialisées (ouvertes) d'un produit à une date donnée pour un réseau donné")
	@GetMapping("/{codeProduit}/commercialisees/{datePourProposerLOffre}/{codeReseau}/v2")
	public ResponseEntity<MicroServiceResponse<List<OffreParam>>> offresCommercialiseesParProduitEtReseauV2(@ApiParam(hidden = true) @RequestAttribute("uuid") final UUID uuidTrace,
			@RequestHeader("app-key") final String appKey, @RequestHeader("app-consumers") final String appConsumers, @PathVariable("codeProduit") final String codeProduit,
			@PathVariable("datePourProposerLOffre") @DateTimeFormat(pattern = "yyyyMMdd") final LocalDate datePourProposerLOffre, @PathVariable("codeReseau") final String codeReseau) {

		componentApiAuthent.verifyV2(appKey, appConsumers, apiAuthent);

		OffreCriteresEligibilite criteres = new OffreCriteresEligibilite();
		criteres.setCodeProduit(codeProduit);
		criteres.setDatePourEmmettreLAffaire(datePourProposerLOffre);
		criteres.setCodeReseau(codeReseau);
		List<OffreParam> offres = component.getOffresParametreesAvecCriteresDEligibilite(criteres, Boolean.FALSE, uuidTrace);
		List<OffreParam> offresEligible = deleteInformationDEligibiliteCommercialisationIfIsEligibleByDateEffetEtCodeReseau(offres, datePourProposerLOffre, codeReseau);
		if (offresEligible.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(new MicroServiceResponse<>(offresEligible), HttpStatus.OK);
	}

	private List<OffreParam> deleteInformationDEligibiliteCommercialisationIfIsEligibleByDateEffetEtCodeReseau(List<OffreParam> offres, LocalDate dateEffet, String codeReseau) {
		offres.stream().filter(Objects::nonNull).filter(o -> !o.getInformationsEligibilite().getReglesExcluantsEligibilite().contains(ReglesEligibilite.DATE_COMMERCIALISATION.getCode()))
				.forEach(o -> o.setInformationsEligibilite(null));
		List<OffreParam> offresEligible = offres.stream().filter(o -> o.getInformationsEligibilite() == null).collect(Collectors.toList());
		offresEligible.stream().forEach(offre -> {
			offre.setCommercialisations(offre.getCommercialisations().stream().filter(o -> fOffre.estCommercialisable(o, dateEffet))
					.filter(c -> codeReseau == null || fOffre.estPossiblePourLeReseau(c, codeReseau)).collect(Collectors.toList()));
		});
		return offresEligible;
	}

	private List<OffreParam> deleteInformationDEligibiliteSouscriptionIfIsEligibleByDateEffetEtCodeReseau(List<OffreParam> offres, LocalDate dateEffet, String codeReseau) {
		offres.stream().filter(Objects::nonNull).filter(o -> !o.getInformationsEligibilite().getReglesExcluantsEligibilite().contains(ReglesEligibilite.DATE_SOUSCRIPTION.getCode()))
				.forEach(o -> o.setInformationsEligibilite(null));
		List<OffreParam> offresEligible = offres.stream().filter(o -> o.getInformationsEligibilite() == null).collect(Collectors.toList());
		offresEligible.stream().forEach(offre -> {
			offre.setCommercialisations(offre.getCommercialisations().stream().filter(o -> fOffre.estSouscriptible(o, dateEffet))
					.filter(c -> codeReseau == null || fOffre.estPossiblePourLeReseau(c, codeReseau)).collect(Collectors.toList()));
		});
		return offresEligible;
	}

	@ApiOperation(value = "Récuperer les offres commercialisees (ouvertes) en fonction des critères")
	@PostMapping("/{codeProduit}/eligibile/commercialisation/v1")
	public ResponseEntity<MicroServiceResponse<List<OffreParam>>> offresCommercialisablesParCriteres(@ApiParam(hidden = true) @RequestAttribute("uuid") final UUID uuidTrace,
			@RequestHeader("app-key") final String appKey, @RequestHeader("app-consumers") final String appConsumers, @PathVariable("codeProduit") final String codeProduit,
			@RequestBody final OffreCriteresEligibilite criteres) {

		componentApiAuthent.verifyV2(appKey, appConsumers, apiAuthent);

		return eligibite(uuidTrace, codeProduit, criteres, Boolean.FALSE);

	}

	@ApiOperation(value = "Récuperer les offres souscriptibles (ouvertes) en fonction des critères")
	@PostMapping("/{codeProduit}/eligibile/souscription/v1")
	public ResponseEntity<MicroServiceResponse<List<OffreParam>>> offresSouscriptiblesParCriteres(@ApiParam(hidden = true) @RequestAttribute("uuid") final UUID uuidTrace,
			@RequestHeader("app-key") final String appKey, @RequestHeader("app-consumers") final String appConsumers, @PathVariable("codeProduit") final String codeProduit,
			@RequestBody final OffreCriteresEligibilite criteres) {

		componentApiAuthent.verifyV2(appKey, appConsumers, apiAuthent);

		return eligibite(uuidTrace, codeProduit, criteres, Boolean.TRUE);

	}

	private ResponseEntity<MicroServiceResponse<List<OffreParam>>> eligibite(final UUID uuidTrace, final String codeProduit, final OffreCriteresEligibilite criteres, Boolean phaseSouscription) {
		if (StringUtils.isNotBlank(criteres.getCodeProduit()) && !StringUtils.equals(codeProduit, criteres.getCodeProduit())) {
			return new ResponseEntity<>(new MicroServiceResponse<>(Arrays.asList("Incohérence sur les datas : codeProduit [" + codeProduit + "] / [" + criteres.getCodeProduit() + "]")),
					HttpStatus.BAD_REQUEST);
		} else if (StringUtils.isBlank(criteres.getCodeProduit())) {
			criteres.setCodeProduit(codeProduit);
		}

		try {
			List<OffreParam> offresEligibles = component.getOffresEligiblesAvecCriteresDEligibilite(criteres, phaseSouscription, uuidTrace);
			List<OffreParam> offresSansInfos = Boolean.TRUE.equals(phaseSouscription)
					? deleteInformationDEligibiliteSouscriptionIfIsEligibleByDateEffetEtCodeReseau(offresEligibles, criteres.getDatePourEmmettreLAffaire(), criteres.getCodeReseau())
					: deleteInformationDEligibiliteCommercialisationIfIsEligibleByDateEffetEtCodeReseau(offresEligibles, criteres.getDatePourEmmettreLAffaire(), criteres.getCodeReseau());

			if (offresSansInfos.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(new MicroServiceResponse<>(offresSansInfos), HttpStatus.OK);
		} catch (EnumNotFoundException e) {
			return new ResponseEntity<>(new MicroServiceResponse<>(Arrays.asList(e.getMessage())), HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation(value = "Récuperer les règles d'ineligibilité (si inéligible) des offres commercialisables en fonction des critères")
	@PostMapping("/{codeProduit}/eligibile/commercialisation/rules/v1")
	public ResponseEntity<MicroServiceResponse<List<OffreParam>>> rulesOffresCommercialisables(@ApiParam(hidden = true) @RequestAttribute("uuid") final UUID uuidTrace,
			@RequestHeader("app-key") final String appKey, @RequestHeader("app-consumers") final String appConsumers, @PathVariable("codeProduit") final String codeProduit,
			@RequestBody final OffreCriteresEligibilite criteres) {

		componentApiAuthent.verifyV2(appKey, appConsumers, apiAuthent);

		return eligibiteRulesOffres(uuidTrace, codeProduit, criteres, Boolean.FALSE);

	}

	@ApiOperation(value = "Récuperer les règles d'ineligibilité (si inéligible) des offres souscriptibles en fonction des critères")
	@PostMapping("/{codeProduit}/eligibile/souscription/rules/v1")
	public ResponseEntity<MicroServiceResponse<List<OffreParam>>> rulesOffresSouscriptibles(@ApiParam(hidden = true) @RequestAttribute("uuid") final UUID uuidTrace,
			@RequestHeader("app-key") final String appKey, @RequestHeader("app-consumers") final String appConsumers, @PathVariable("codeProduit") final String codeProduit,
			@RequestBody final OffreCriteresEligibilite criteres) {

		componentApiAuthent.verifyV2(appKey, appConsumers, apiAuthent);

		return eligibiteRulesOffres(uuidTrace, codeProduit, criteres, Boolean.TRUE);
	}

	private ResponseEntity<MicroServiceResponse<List<OffreParam>>> eligibiteRulesOffres(final UUID uuidTrace, final String codeProduit, final OffreCriteresEligibilite criteres,
			Boolean phaseSouscription) {

		if (StringUtils.isNotBlank(criteres.getCodeProduit()) && !StringUtils.equals(codeProduit, criteres.getCodeProduit())) {
			return new ResponseEntity<>(new MicroServiceResponse<>(Arrays.asList("Incohérence sur les datas : codeProduit [" + codeProduit + "] / [" + criteres.getCodeProduit() + "]")),
					HttpStatus.BAD_REQUEST);
		} else if (StringUtils.isBlank(criteres.getCodeProduit())) {
			criteres.setCodeProduit(codeProduit);
		}

		try {
			List<OffreParam> offres = component.getOffresAvecCriteresDEligibilite(criteres, phaseSouscription, uuidTrace);
			if (offres.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(new MicroServiceResponse<>(offres), HttpStatus.OK);
		} catch (EnumNotFoundException e) {
			return new ResponseEntity<>(new MicroServiceResponse<>(Arrays.asList(e.getMessage())), HttpStatus.BAD_REQUEST);
		}
	}

}
